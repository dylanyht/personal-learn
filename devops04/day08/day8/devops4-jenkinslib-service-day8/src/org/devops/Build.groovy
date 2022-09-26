package org.devops

def MavenBuild(){
    sh "/data/devops4/tools/apache-maven-3.8.5/bin/mvn clean package"
}

def GradleBuild(){
    sh "/data/devops4/tools/gradle-7.4.2/bin/gradle build"
}

def NpmBuild(){
    sh "npm install && npm run build"
}

def YarnBuild(){
    sh "yarn"
}

def CodeBuild(type){
    switch(type){
        case "maven":
            MavenBuild()
            break;
        case "gradle":
            GradleBuild()
            break;
        case "npm":
            NpmBuild()
            break;
        case "yarn":
            YarnBuild()
            break;
        default:
            error "No such tools ... [maven/ant/gradle/npm/yarn/go]"
            break
    }
}
