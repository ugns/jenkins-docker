/*
   This script will configure global settings for a Jenkins instance.  These
   are all settings built into core.
   Configures Global settings:
     - Jenkins URL
     - Admin email
     - Number of master executors
 */

import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration

def env = System.getenv()

String frontend_url = env['JENKINS_URL'] ?: 'http://localhost:8080/'
String admin_email = env['JENKINS_ADMIN_EMAIL']
def master_executors = env['JENKINS_MASTER_EXECUTORS'] ?: 2

Jenkins j = Jenkins.instance
JenkinsLocationConfiguration location = j.getExtensionList('jenkins.model.JenkinsLocationConfiguration')[0]
Boolean save = false

if(location.url != frontend_url) {
    println "Updating Jenkins URL to: ${frontend_url}"
    location.url = frontend_url
    save = true
}
if(admin_email && location.adminAddress != admin_email) {
    println "Updating Jenkins Email to: ${admin_email}"
    location.adminAddress = admin_email
    save = true
}
if(j.numExecutors != master_executors.toInteger()) {
    println "Setting master num executors to: ${master_executors}"
    j.numExecutors = master_executors.toInteger()
    save = true
}

//save configuration to disk
if(save) {
    j.save()
    location.save()
}
else {
    println 'Nothing changed.  Jenkins settings already configured.'
}
