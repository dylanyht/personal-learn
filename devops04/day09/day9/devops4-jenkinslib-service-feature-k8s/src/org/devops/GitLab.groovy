package org.devops


// 封装HTTP
def HttpReqByHttpRequest(reqType, reqUrl,reqBody ){
    def gitServer = "http://192.168.1.200:8077/api/v4"
    withCredentials([string(credentialsId: '553fcb38-81e1-4cdd-aa60-a8886a2eb323', variable: 'GITLABTOKEN')]) {
        response = httpRequest acceptType: 'APPLICATION_JSON_UTF8', 
                          consoleLogResponseBody: true, 
                          contentType: 'APPLICATION_JSON_UTF8', 
                          customHeaders: [[maskValue: false, name: 'PRIVATE-TOKEN', value: "${GITLABTOKEN}"]], 
                          httpMode: "${reqType}", 
                          url: "${gitServer}/${reqUrl}", 
                          wrapAsMultipart: false,
                          requestBody: "${reqBody}"

    }
    return response
}

//更新文件内容
def UpdateRepoFile(projectId,filePath,fileContent, branchName){
    apiUrl = "projects/${projectId}/repository/files/${filePath}"
    reqBody = """{"branch": "${branchName}","encoding":"base64", "content": "${fileContent}", "commit_message": "update a new file"}"""
    response = HttpReqByHttpRequest('PUT',apiUrl,reqBody)
    println(response)

}


//创建文件
def CreateRepoFile(projectId,filePath,fileContent, branchName){
    apiUrl = "projects/${projectId}/repository/files/${filePath}"
    reqBody = """{"branch": "${branchName}","encoding":"base64", "content": "${fileContent}", "commit_message": "update a new file"}"""
    response = HttpReqByHttpRequest('POST',apiUrl,reqBody)
    println(response)

}

//获取文件内容
def GetRepoFile(projectId,filePath, branchName ){
   //GET /projects/:id/repository/files/:file_path/raw
   apiUrl = "/projects/${projectId}/repository/files/${filePath}/raw?ref=${branchName}"
   response = HttpReq('GET', apiUrl)
   return response
}


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
            http://192.168.1.200:8077/api/v4/${apiUrl} \
            --header 'PRIVATE-TOKEN: N9mvJV4hq-z7yCcYEsC-' 
        """
    return response

}


def GetProjectID(projectName, groupName){
    response = sh  returnStdout: true, 
        script: """ 
            curl --location --request GET \
            http://192.168.1.200:8077/api/v4/projects?search=${projectName} \
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
    response = readJSON text: response - "\n"
    return response.commit.short_id
}
