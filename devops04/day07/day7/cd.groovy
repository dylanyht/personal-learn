
pipeline {
    agent {
        label "build"
    }

    stages {
        stage("PullArtifact"){
            steps{
                script {
                    env.buName = "${JOB_NAME}".split("-")[0]
                    env.serviceName = "${JOB_NAME}".split("_")[0]


                    projectID = GetProjectID("${env.serviceName}", "${env.buName}")
                    commitID = GetBranchCommitID(projectID, "${env.branchName}")
                    currentBuild.description = """ branchName: ${env.branchName} \n"""
                    currentBuild.displayName = "${commitID}"

                    path = "${env.buName}/${env.serviceName}/${env.branchName}-${commitID}"
                    pkgName = "${env.serviceName}-${env.branchName}-${commitID}.jar"
                    PullArtifact(path, pkgName)
                }
            }
        }

        stage("Deploy"){
            steps{
                script{
                    println("deploy  ${env.envList}")
                }
            }
        }
    }
}


def PullArtifact(path, pkgName){
    // path = devops4/devops4-maven-service/RELEASE-1.1.1-03dfb8ee
    // pkgName = devops4-maven-service-RELEASE-1.1.1-03dfb8ee.jar

    sh """

    curl http://192.168.1.200:8081/repository/devops4-local/${path}/${pkgName} \
    -u admin:admin123 \
    -o ${pkgName}  -s 

    """
}

def HttpReq(method, apiUrl){
    response = sh  returnStdout: true, 
        script: """ 
            curl --location --request ${method} \
            http://192.168.1.200/api/v4/${apiUrl} \
            --header 'PRIVATE-TOKEN: N9mvJV4hq-z7yCcYEsC-' 
        """
    response = readJSON text: response - "\n"
    return response

}


def GetProjectID(projectName, groupName){
    response = sh  returnStdout: true, 
        script: """ 
            curl --location --request GET \
            http://192.168.1.200/api/v4/projects?search=${projectName} \
            --header 'PRIVATE-TOKEN: N9mvJV4hq-z7yCcYEsC-' 
        """
    response = readJSON text: response
    if (response != []){
        for (p in response) {
            if (p["namespace"]["name"] == groupName){
                return response[0]["id"]
            }
        }
    }
}

def GetBranchCommitID(projectID, branchName){
    apiUrl = "projects/${projectID}/repository/branches/${branchName}"
    response = HttpReq("GET", apiUrl)
    return response.commit.short_id
}