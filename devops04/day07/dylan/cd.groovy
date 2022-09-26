pipeline{
    agent {
        label "build"

    }

    stages{
        stage("PullArtifact"){
            steps{
                script{
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
                    println("deploy ${env.envList}")
                }
            }
        }
    }
}

//下载制品
def PullArtifact(path, pkgName){
    // path = devops4/devops4-maven-service/main-435a3a55
    // pkgName = devops4-maven-service-main-435a3a55.jar
    sh """
    curl http://172.16.77.64:9081/repository/devops4-local/${path}/${pkgName} \
    -u admin:admin000 \
    -o ${pkgName} -s
    """
}

def HttpReq(method, apiUrl){
    response = sh returnStdout: true,
       script: """
                  curl --location --request ${method}  \
                   'http://git.bltest.cameobespoke.com/api/v4/${apiUrl}' \
                    --header 'PRIVATE-TOKEN: ekBVaNWJshUDXhfwYtEf'        """
    response = readJSON text: response - "\n"
    return response

}

def GetProjectID(projectName, groupName){
    response = sh returnStdout: true,
       script: """
                  curl --location --request GET \
                   'http://git.bltest.cameobespoke.com/api/v4/projects?search=${projectName}' \
                    --header 'PRIVATE-TOKEN: ekBVaNWJshUDXhfwYtEf'        """
       response = readJSON text: response
       if (response != [] ) {
        for (p in response ){
            if (p["namespace"]["name"] ==  groupName ){
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