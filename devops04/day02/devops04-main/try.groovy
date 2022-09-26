pipeline {
	agent { 
		node {
			label "build" 
			customWorkspace "/opt/aa"
		}
	}

	stages {
		stage("demo"){
			steps {
				script {

					try {
						sh "mvn cleaner "
					}
					catch(Exception e) {
						println(e)
						currentBuild.description = "mvn error"
					}
					finally {
						println("catch error ....")
					}
					
					
				}
			}
		}
	}
}