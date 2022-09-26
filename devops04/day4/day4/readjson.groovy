
pipeline {
    agent any

    stages {
        stage('Hello') {
            steps {
                script{
                
                    echo "data: ${webhookData}"
                    
                    data = readJSON text: "${webhookData}"
                    println(data.version)
                }
            }
        }
    }
}
