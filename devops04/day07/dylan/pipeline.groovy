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
                    profileName = "${JOB_NAME}".split("-")[0]
                    sonar.Init("${JOB_NAME}", "java", profileName )
                    //commit-status
                    commitID = gitcli.GetCommitID()
                    groupName = profileName
                    projectID = gitcli.GetProjectID("${JOB_NAME}", groupName)

                    sonar.CodeScan("${env.branchName}", commitID, projectID)   
                }

            }
        }
        stage("PushArtifact"){
            steps{
                script{
                    //读取pom文件获取坐标信息
                    // pomData = readMavenPom file: pom.xml
                    // println(pomData)
                    // //com.example:demo:jar:0.0.1-SNAPSHOT
                    // groupId = pomData.split(":")[0]
                    // artifactId = pomData.split(":")[1]
                    // type = pomData.split(":")[2]
                    // version = pomData.split(":")[3]
                    buName = "${JOB_NAME}".split('-')[0]
                    repoName = "${buName}-snapshot"
                    file = "target/${env.artifactId}-${env.version}.${env.packaging}"
                    //用户输入获取坐标信息
                   //PushArtifactByNexusPlugin(env.artifactId,file, env.packaging, env.groupId, repoName, env.version)

                   // PushArtifactByMavenCLI(env.artifactId,file, env.packaging, env.groupId, repoName, env.version)
                    JarName = sh returnStdout: true, script: """ls target | grep -E "jar\$" """
                    file = "target/" + JarName -"\n"
                    println(file)
                    
                    PushArtifactByMavenPom(repoName,file)
                }
            }
        }
    }
}

def PushArtifactByMavenPom(repoName,file){
    sh """
        mvn deploy:deploy-file \
        -DgeneratePom=true \
        -DrepositoryId=mymaven \
        -Durl=http://172.16.77.64:9081/repository/${repoName}/  \
        -DpomFile=pom.xml \
        -Dfile=${file}
    """
}

def PushArtifactByMavenCLI(artifactId, file, type, groupId, repoName, version){
    sh """
      mvn deploy:deploy-file \
        -DgroupId=${groupId} \
        -DartifactId=${artifactId} \
        -Dversion=${version} \
        -Dpackaging=${type}  \
        -Dfile=${file} \
        -Durl=http://172.16.77.64:9081/repository/${repoName}/  \
        -DrepositoryId=mymaven

    """
}

def PushArtifactByNexusPlugin(artifactId, file, type, groupId, repoName, version){
    nexusArtifactUploader artifacts: [[artifactId: artifactId, 
                                   classifier: '', 
                                   file: file, 
                                   type: type]], 
                                   credentialsId: '4f27869b-c5c8-4906-8198-49390c8ea1df', 
                                   groupId: groupId, 
                                   nexusUrl: '172.16.77.64:9081', 
                                   nexusVersion: 'nexus3', 
                                   protocol: 'http', 
                                   repository: 'devops4-release', 
                                   version: version
}

