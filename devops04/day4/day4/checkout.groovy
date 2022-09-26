pipeline {
    agent any

    stages {
        stage('CheckOut') {
            steps {
                script{
                
                    checkout([$class: 'GitSCM', 
                        branches: [[name: 'main']], 
                        extensions: [], 
                        userRemoteConfigs: [[credentialsId: '62a3150f-38f8-4c9d-9334-79072b1d75cc', 
                                            url: 'http://192.168.1.200/devops4/devops4-commit-service.git']]])
                }
            }
        }
    }
}
