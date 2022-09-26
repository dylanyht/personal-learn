

pipeline  {
	agent {
	    label "build"
	}

	stages{
		stage("run"){
			steps {
				script {
					projectName = "devops4-maven-service"
					result = ProjectSearch(projectName)
					println(result)

					if (result == false){
						CreateProject(projectName)
					}

					UpdateQualityProfiles("java", projectName, "devops4")
				}
			}
		}
	}
}


// 更新质量配置
def UpdateQualityProfiles(lang, projectName, profileName){
    apiUrl = "qualityprofiles/add_project?language=${lang}&project=${projectName}&qualityProfile=${profileName}"
    response = SonarRequest(apiUrl,"POST")
    
    if (response.errors != true){
        println("ERROR: UpdateQualityProfiles ${response.errors}...")
        return false
    } else {
        println("SUCCESS: UpdateQualityProfiles ${lang} > ${projectName} > ${profileName}" )
        return true
    }
}

// 创建项目
def CreateProject(projectName){
    apiUrl = "projects/create?name=${projectName}&project=${projectName}"
    response = SonarRequest(apiUrl,"POST")
    try{
        if (response.project.key == projectName ) {
            println("Project Create success!...")
            return true
        }
    }catch(e){
        println(response.errors)
        return false
    }
}

// 查找项目
def ProjectSearch(projectName){
    apiUrl = "projects/search?projects=${projectName}"
    response = SonarRequest(apiUrl,"GET")

    if (response.paging.total == 0){
        println("Project not found!.....")
        return false
    } 
    return true
}

def SonarRequest(apiUrl,method){
    withCredentials([string(credentialsId: "9fd9069d-25a9-4d5d-b29a-e61183deb525", variable: 'SONAR_TOKEN')]) {
        sonarApi = "http://192.168.1.200:9000/api"
        response = sh  returnStdout: true, 
            script: """
            curl --location \
                 --request ${method} \
                 "${sonarApi}/${apiUrl}" \
                 --header "Authorization: Basic ${SONAR_TOKEN}"
            """
        try {
            response = readJSON text: """ ${response - "\n"} """
        } catch(e){
            response = readJSON text: """{"errors" : true}"""
        }
        return response
    }
}


