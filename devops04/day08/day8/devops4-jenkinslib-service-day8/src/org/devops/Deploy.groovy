package org.devops


def SaltDeploy(){
    localDeployDir = "/srv/salt/${env.projectName}"
    sh """
        [ -d ${localDeployDir} ] || mkdir -p ${localDeployDir}
        mv ${env.pkgName} ${localDeployDir}
        # 清理发布目录
        salt -L "${env.deployHosts}" cmd.run  "rm -fr ${env.targetDir}/${env.projectName}/* &&  mkdir -p ${env.targetDir}/${env.projectName} || echo file is exists"

        # 发布应用
        salt -L "${env.deployHosts}" cp.get_file salt://${env.projectName}/${env.pkgName} ${env.targetDir}/${env.projectName}/
    """

    // 发布脚本
    fileData = libraryResource 'scripts/service.sh'
    //println(fileData)
    writeFile file: 'service.sh', text: fileData
    sh "ls -a ; cat service.sh "

    sh """
        mv service.sh  ${localDeployDir}
        # 发布应用
        salt -L "${env.deployHosts}" cp.get_file salt://${env.projectName}/service.sh ${env.targetDir}/${env.projectName}/
        # 启动服务
        salt -L "${env.deployHosts}" cmd.run  "cd ${env.targetDir}/${env.projectName} ;source /etc/profile  && sh service.sh ${env.projectName} ${env.releaseVersion} ${env.port} start"
        # 检查服务
        sleep 5
        salt -L "${env.deployHosts}" cmd.run  "cd ${env.targetDir}/${env.projectName} ;source /etc/profile  && sh service.sh ${env.projectName} ${env.releaseVersion} ${env.port} check"
    """
}


def AnsibleDeploy(){
    //将主机写入清单文件
    sh "rm -fr hosts"
    for (host in "${env.deployHosts}".split(',')){
        sh " echo ${host} >> hosts"
    }

    // ansible 发布jar
    sh """
        # 主机连通性检测
        ansible "${env.deployHosts}" -m ping -i hosts 
        # 清理和创建发布目录
        ansible "${env.deployHosts}" -m shell -a "rm -fr ${env.targetDir}/${env.projectName}/* &&  mkdir -p ${env.targetDir}/${env.projectName} || echo file is exists" 
        # 复制app
        ansible "${env.deployHosts}" -m copy -a "src=${env.pkgName}  dest=${env.targetDir}/${env.projectName}/${env.pkgName}" 
    """
    // 发布脚本
    fileData = libraryResource 'scripts/service.sh'
    //println(fileData)
    writeFile file: 'service.sh', text: fileData
    //sh "ls -a ; cat service.sh "

    sh """
        # 复制脚本
        ansible "${env.deployHosts}" -m copy -a "src=service.sh  dest=${env.targetDir}/${env.projectName}/service.sh" 

        # 启动服务
        ansible "${env.deployHosts}" -m shell -a "cd ${env.targetDir}/${env.projectName} ;source /etc/profile  && sh service.sh ${env.projectName} ${env.releaseVersion} ${env.port} start" -u root

        # 检查服务 
        sleep 10
        ansible "${env.deployHosts}" -m shell -a "cd ${env.targetDir}/${env.projectName} ;source /etc/profile  && sh service.sh ${env.projectName} ${env.releaseVersion} ${env.port} check" -u root
    """
}
