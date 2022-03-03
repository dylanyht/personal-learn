pipeline{
    agent{ label "build"}

    stages{
        stage("CheckOut"){
            steps{
                script{
                    //仓库信息
                    branchName = "${params.branchName}"
                    srcUrl = "${params.srcUrl}"

                    //下载代码
                    checkout([
                        $class: 'GitSCM', 
                        branches: [[name: branchName]], 
                        extensions: [], 
                        userRemoteConfigs: [[
                            credentialsId: 'd99785ec-29f6-4a75-9970-c9a49553366a', 
                            url: 'http://git.bltest.cameobespoke.com/devops03/devops03-demo-service.git']]])
                    
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