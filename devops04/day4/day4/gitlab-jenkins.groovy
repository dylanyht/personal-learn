println("${webhookData}")

webhookData = readJSON text: "${webhookData}"


env.srcUrl = webhookData.project.git_http_url
env.branchName = webhookData.ref - "refs/heads/"
env.commitId = webhookData.checkout_sha[0..7]

currentBuild.description = """
srcurl: ${env.srcUrl} \n 
branchName: ${env.branchName} \n
"""
currentBuild.displayName = "${env.commitId}"

env.userEmail = webhookData.user_email

pipeline {
    agent any

    stages {
        stage('CheckOut') {
            steps {
                script{
                
                    checkout([$class: 'GitSCM', 
                        branches: [[name: "${env.branchName}"]], 
                        extensions: [], 
                        userRemoteConfigs: [[credentialsId: '62a3150f-38f8-4c9d-9334-79072b1d75cc', 
                                            url: "${env.srcUrl}"]]])
                }
            }
        }

        stage("build"){
            steps{
                script{
                    echo "build"
                }
            }
        }

        stage("test"){
            steps{
                script{
                    echo "test"
                }
            }
        }

        stage("deploy"){
            steps {
                script{
                    echo "deploy"
                }
            }
        }
    }

    post {
        always {
            script {
                EmailUser("${env.userEmail}", currentBuild.result)
                curl 
            }
        }
    }
}


def EmailUser(userEmail,status){
    emailext body: """
            <!DOCTYPE html> 
            <html> 
            <head> 
            <meta charset="UTF-8"> 
            </head> 
            <body leftmargin="8" marginwidth="0" topmargin="8" marginheight="4" offset="0"> 
                <img src="http://192.168.1.200:8080/static/714d6c37/images/svgs/logo.svg">
                <table width="95%" cellpadding="0" cellspacing="0" style="font-size: 11pt; font-family: Tahoma, Arial, Helvetica, sans-serif">   
                    <tr> 
                        <td><br /> 
                            <b><font color="#0B610B">构建信息</font></b> 
                        </td> 
                    </tr> 
                    <tr> 
                        <td> 
                            <ul> 
                                <li>项目名称：${JOB_NAME}</li>         
                                <li>构建编号：${BUILD_ID}</li> 
                                <li>构建状态: ${status} </li>                         
                                <li>项目地址：<a href="${BUILD_URL}">${BUILD_URL}</a></li>    
                                <li>构建日志：<a href="${BUILD_URL}console">${BUILD_URL}console</a></li> 
                            </ul> 
                        </td> 
                    </tr> 
                    <tr>  
                </table> 
            </body> 
            </html>  """,
            subject: "Jenkins-${JOB_NAME}项目构建信息 ",
            to: userEmail

}
