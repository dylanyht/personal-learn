package org.devops

//下载代码
def GetCode( srcUrl, branchName){
    checkout([$class: 'GitSCM', 
                        branches: [[name: branchName]], 
                        extensions: [], 
                        userRemoteConfigs: [[credentialsId: '62a3150f-38f8-4c9d-9334-79072b1d75cc', 
                                            url: srcUrl]]])
}
