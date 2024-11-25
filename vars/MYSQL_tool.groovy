def call(String branch, String repoUrl) {

pipeline {
    agent any
    
    tools {
        ansible 'ansible'
    }
    
    // environment {
    //    ANSIBLE_ROLES_PATH = "${WORKSPACE}/roles" // Set roles path dynamically
    //}
    
        //-u ${ANSIBLE_USER} --extra-vars "ansible_password=${ANSIBLE_PASSWORD}" 
    
    stages{
        stage('clone'){
            steps{
               // echo "git......!! ${WORKSPACE} "
                  echo "Cloning repository..."
                    checkout([$class: 'GitSCM',
                              branches: [[name: "*/${branch}"]],
                              userRemoteConfigs: [[url: repoUrl]]])
               // git branch: 'main' , url: 'https://github.com/aayushverma191/ansible_tool.git'
            }
        }
        stage('User_Approval'){
            steps{
                input message: 'do you want to install MYSQL', ok: 'Approved'
                echo "Approval Done "
            }
        }
        stage('Playbook_Execution'){
            steps {
                script{
                     //withCredentials([usernamePassword(credentialsId: 'ansible-credentials', 
                     //usernameVariable: 'ANSIBLE_USER', passwordVariable: 'ANSIBLE_PASSWORD')]) 
                    //ansible-playbook -i ${WORKSPACE}/inventory ${WORKSPACE}/tool.yml --private-key ${WORKSPACE}/tool/ninja.pem
                    //{
                        sh """
                            ansible-playbook -i /var/lib/jenkins/workspace/assignment6/mysql-tool/inventory /var/lib/jenkins/workspace/assignment6/mysql-tool/tool.yml --private-key /var/lib/jenkins/workspace/assignment6/mysql-tool/tool/ninja.pem
                        """
                    //}
                }
            }
        }
    }
   post {
         success {
                 slackSend(channel: 'info', message: "Build Successful: JOB-Name:- ${JOB_NAME} Build_No.:- ${BUILD_NUMBER} & Build-URL:- ${BUILD_URL}")
             }
         failure {
                 slackSend(channel: 'info', message: "Build Failure: JOB-Name:- ${JOB_NAME} Build_No.:- ${BUILD_NUMBER} & Build-URL:- ${BUILD_URL}")
             }
     }
}
}
