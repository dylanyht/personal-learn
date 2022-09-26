@Library("mylib@main") _     //加载共享库
import org.devops.*						// 导入库

def checkout = new Checkout()    //New实例化
def build = new Build()
def unittest = new UnitTest()
def sonar = new Sonar()
def gitcli = new GitLab()

//env.buildType = "${JOB_NAME}".split("-")[1]



//流水线
pipeline {
    agent { label "build" }

    options {
        skipDefaultCheckout true
    }

    stages{
        stage("Checkout"){
            steps{
                script {
                    println("GetCode")
                    checkout.GetCode("${env.srcUrl}", "${env.branchName}")
                    env.commitID = gitcli.GetCommitID()
                    env.buName = "${JOB_NAME}".split("-")[0]
                    env.serviceName = "${JOB_NAME}"
                    currentBuild.description = """ branchName: ${env.branchName} \n"""
                    currentBuild.displayName = "${env.commitID}"
                }
            }
        }

        stage("Build"){
            steps{
                script{
                    println("Build")
                    //build.CodeBuild("${env.buildType}")
                    sh "${env.buildShell}"
                }
            }
        }

        /*stage("UnitTest"){
            steps{
                script{
                    unittest.CodeTest("${env.buildType}")
                }
            }
        }*/

        stage("CodeScan"){
            when {
		        environment name: 'skipSonar', value: 'false'
	        }
            steps{
                script{
                    //代码扫描
                    profileName= "${JOB_NAME}".split("-")[0]
                    sonar.Init("${JOB_NAME}", "java", profileName )
                    //commit-status
            
                    groupName = profileName
                    projectID = gitcli.GetProjectID("${JOB_NAME}", groupName)

                    sonar.CodeScan("${env.branchName}", env.commitID, projectID)   
                    println("${env.commitID}")           
                }
            }
        }

        stage("PushArtifact"){
            steps{
                script{        
                    //Dir /buName/serviceName/version/serviceName-version.xxx
                   
                    version = "${env.branchName}-${env.commitID}"

                    // 重命名
                    JarName = sh returnStdout: true, script: """ls target | grep -E "jar\$" """
                    fileName = JarName -"\n"
                    fileType = fileName.split('\\.')[-1]
                    newFileName = "${env.serviceName}-${version}.${fileType}"

                    sh "cd target ; mv ${fileName}  ${newFileName} "
                    PushArtifact("${env.buName}/${env.serviceName}/${version}", "target/${newFileName}")
                    
                    
                }
            }
        }
    }
}


def PushArtifact(targetDir, filePath){
    //上传制品
    sh """
        curl -X POST "http://192.168.1.200:8081/service/rest/v1/components?repository=devops4-local" \
        -H "accept: application/json" \
        -H "Content-Type: multipart/form-data" \
        -F "raw.directory=${targetDir}" \
        -F "raw.asset1=@${filePath};type=application/java-archive" \
        -F "raw.asset1.filename=${newFileName}" \
        -u admin:admin123
    """
}