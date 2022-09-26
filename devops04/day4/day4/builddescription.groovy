
pipeline {
    agent any

    stages {
        stage('Hello') {
            steps {
                script{
                
                    echo "data: ${webhookData}"
                    
                    data = readJSON text: "${webhookData}"
                    println(data.version)
                    
                    currentBuild.description = "version: ${data.version}\n env: ${data.envName}"
                    currentBuild.displayName = "${data.version}"
                }
            }
        }
    }
}
