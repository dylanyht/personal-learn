package org.devops

//Maven
def MavenTest(){
    sh "mvn test "
    junit 'target/surefire-reports/*.xml'
}

//Gradle
def GradleTest(){
   sh "gradle test"
   junit 'build/test-results/test/*.xml'
}

//Ant
//def AntBuild(configPath="./build.xml"){
//    sh "ant -f ${configPath}"
//}

//Golang
def GoTest(){
    sh " go test"
}

//Npm
def NpmTest(){
    sh "npm test"
}

//Yarn
def YarnTest(){
    sh "yarn test "
}

//Main
def CodeTest(type){
    switch(type){
        case "maven":
            MavenTest()
            break;
        case "gradle":
            GradleTest()
            break;
        default:
            println("No such tools ... [maven/ant/gradle/npm/yarn/go]")
            break
    }
}
