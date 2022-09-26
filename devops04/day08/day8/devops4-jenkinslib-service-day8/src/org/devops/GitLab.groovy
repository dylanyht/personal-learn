package org.devops

//获取CommitID
def GetCommitID(){
    ID = sh returnStdout: true, script:"git rev-parse HEAD"
    ID = ID -"\n"
    return ID[0..7] 
}

// 获取commitID （API）
//获取ProjectID
// fork
// namespace 
// usera/devops-service-app
// userb/devops-service-app 
/*def GetProjectID(projectName, groupName){
    response = sh  returnStdout: true, 
        script: """ 
            curl --location --request GET \
            http://192.168.1.200/api/v4/projects?search=${projectName} \
            --header 'PRIVATE-TOKEN: N9mvJV4hq-z7yCcYEsC-' \
            --header 'Authorization: Basic YWRtaW46YWRtaW4xMjM='
        """
    response = readJSON text: response
    if (response != []){
        for (p in response) {
            if (p["namespace"]["name"] == groupName){
                return response[0]["id"]
            }
        }
    }
}*/

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
