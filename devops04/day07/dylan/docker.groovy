
pipeline {
    agent {
        label "build"
    }

    stages {
        stage("DockerBuild"){
            steps{
                script{
                  imageName = "${env.buName}/${env.serviceName}"
                  imageTag = "${env.branchName}-${env.commitID}"
                  sh """
                    #登录镜像仓库
                    docker login -u admin -p Admin000 harbor.bltest.kutesmart.cn

                    # 构建镜像
                    docker build -t harbor.bltest.kutesmart.cn/${imageName}:${imageTag} .

                    # 上传镜像
                    docker push harbor.bltest.kutesmart.cn/${imageName}:${imageTag}

                    # 删除镜像
                    sleep 2
                    docker rmi harbor.bltest.kutesmart.cn/${imageName}:${imageTag}
                  """
                    }
                }
            }
        }
    }
}