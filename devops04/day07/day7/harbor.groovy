import groovy.json.JsonSlurper

/*
清理docker镜像

1. 获取镜像列表
2. 用户选择删除
3. 调用api删除

*/
pipeline {
    agent {
        label "build"
    }

    stages{

        stage("GetTags"){
            steps{
                script{
                    env.projectName = "${env.repoName}".split("-")[0]
                    env.result = GetArtifactTag(env.projectName, env.repoName)
                    env.result = env.result - '[' - ']'
                }
            }
        }

        stage("Clean"){
            steps{
                script{

                    def result = input  message: "是否删除${env.projectName}项目的${env.repoName}这些标签：", 
                                        parameters: [extendedChoice(defaultValue: "${env.result}", 
                                                                    multiSelectDelimiter: ',', 
                                                                    name: 'taga', 
                                                                    quoteValue: false, 
                                                                    saveJSONParameterToFile: false, 
                                                                    type: 'PT_CHECKBOX', 
                                                                    value: "${env.result}", 
                                                                    visibleItemCount: 20)]
                    println("${result}")
                    // println("Delete  ${taga}, doing.......")
                    // tags = "${taga}" - '[' - ']'

                    for(t in result.split(',')){
                        println("Delete >>>>" + t.trim())
                        DeleteArtifactTag(env.projectName,env.repoName, t.trim())
                    }
                }
            }

        }
    }
}


// 删除镜像tag
def DeleteArtifactTag(projectName,repoName, tagName){
    harborAPI = "http://192.168.1.200:8088/api/v2.0/projects/${projectName}/repositories/${repoName}"
    apiURL = "artifacts/${tagName}/tags/${tagName}"
    sh """ curl -X DELETE "${harborAPI}/${apiURL}" -H "accept: application/json"  -u admin:Harbor12345 """
}

// 获取镜像的所有标签
// acmp-nginx-service
def GetArtifactTag(projectName,repoName ){
    harborAPI = "http://192.168.1.200:8088/api/v2.0/projects/${projectName}/repositories/${repoName}"
    apiURL = "artifacts?page=1&page_size=10"
    response = sh returnStdout: true, script:  """curl -X GET "${harborAPI}/${apiURL}" -H "accept: application/json" -u admin:Harbor12345 """
    response = readJSON text: """${response - "\n"}""" 
    tags = []
    for (t in response[0].tags){
        tags << t.name
    }

    return tags
}