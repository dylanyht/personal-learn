pipeline{
    //指定运行此流水线的节点
    agent {
        node{
            label "build"
        }
    }

    environment{
        branchName = "dev"
        version = "1.1.1"
    }

    stages{
        stage("Checkout"){
            environment{
                branchName = "test"
            }
            steps{
                script{
                    echo "${branchName}"
                }
            }
        }
        stage("Build"){
            steps{
                script{
                    println("运行构建")
                    echo "${version}"
                    echo "branchName: ${branchName}"
                }
            }
        }
    }
}