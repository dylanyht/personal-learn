package org.devops

def PushArtifact(targetDir, filePath, fileName){
    //上传制品
    sh """
        curl -X POST "http://192.168.1.200:8081/service/rest/v1/components?repository=devops4-local" \
        -H "accept: application/json" \
        -H "Content-Type: multipart/form-data" \
        -F "raw.directory=${targetDir}" \
        -F "raw.asset1=@${filePath}/${fileName};type=application/java-archive" \
        -F "raw.asset1.filename=${fileName}" \
        -u admin:admin123
    """
}
//通过maven命令读取pom上传
def PushArtifactByMavenPom(repoName,file){
    sh """
        mvn deploy:deploy-file \
        -Dfile=${file} \
        -DpomFile=pom.xml \
        -Durl=http://192.168.1.200:8081/repository/${repoName}  \
        -DrepositoryId=mymaven
    """
}

// 通过maven命令上传
def PushArtifactByMavenCLI(artifactId,file, type, groupId, repoName, version){
    sh """
        mvn deploy:deploy-file \
    -DgroupId=${groupId} \
    -DartifactId=${artifactId} \
    -Dversion=${version} \
    -Dpackaging=${type} \
    -Dfile=${file} \
    -Durl=http://192.168.1.200:8081/repository/${repoName}  \
    -DrepositoryId=mymaven
"""
}


//通过nexus插件上传
def PushArtifactByNexusPlugin(artifactId,file, type, groupId, repoName, version){
    // println(artifactId)
    // println("${file}, ${type}, ${groupId}, ${repoName}, ${version}")
    nexusArtifactUploader artifacts: [[artifactId: artifactId , 
                                classifier: '', 
                                file: file, 
                                type: type]], 
                      credentialsId: '3c88cd60-4e1e-4a7f-9dfe-33eeed6a07e5', 
                      groupId: groupId, 
                      nexusUrl: '192.168.1.200:8081', 
                      nexusVersion: 'nexus3', 
                      protocol: 'http', 
                      repository: repoName, 
                      version: version
}

