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
                    docker login -u admin -p Harbor12345 192.168.1.200:8088

                    # 构建镜像
                    docker build -t 192.168.1.200:8088/${imageName}:${imageTag} .

                    # 上传镜像
                    docker push 192.168.1.200:8088/${imageName}:${imageTag}

                    # 删除镜像
                    sleep 2
                    docker rmi 192.168.1.200:8088/${imageName}:${imageTag}
                  """
                    }
                }
            }
        }
    }
}


