



//gitlab传递的数据
println("${WebhookData}")

//数据格式化
WebHookData = readJSON text: "${WebhookData}"

//提取仓库信息
env.srcUrl = WebHookData["project"]["git_http_url"]  //项目地址
env.branchName = WebHookData["ref"] - "refs/heads/" //分支
env.commitId = WebHookData["checkout_sha"]         //提交ID
env.commitUser = WebHookData["user_username"]      //提交人
env.userEmail = WebHookData["user_email"]          //邮箱

currentBuild.description = "Trigger by Gitlab \n branch: ${env.branchName} \n user: ${env.commitUser}"
currentBuild.displayName = "${env.commitId}"



pipeline{
    agent{ label "build"}

    stages{
        stage("CheckOut"){
            steps{
                script{
                    //仓库信息
                    // branchName = "${params.branchName}"
                    // srcUrl = "${params.srcUrl}"

                    //下载代码
                    checkout([
                        $class: 'GitSCM', 
                        branches: [[name: "${env.branchName}"]], 
                        extensions: [], 
                        userRemoteConfigs: [[
                            credentialsId: 'd99785ec-29f6-4a75-9970-c9a49553366a', 
                            url: "${env.srcUrl}"]]])
                    
                    //验证
                    sh " ls -l "
                }
            }

        }

            //代码构建
        stage("Build"){
            steps{
                script{
                    echo "build"
                }
            }
        }

            //单元测试
        stage("UnitTest"){
            steps{
                script{
                    echo "unit test"
                }
            }
        }
    }
}