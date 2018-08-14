/*
   This script will configure global settings for a Jenkins instance.  These
   are all settings built into core.
   Configures Global settings:
     - Jenkins URL
     - Admin email
     - Number of master executors
     - TCP port for JNLP slave agents
 */

import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration

def env = System.getenv()

String frontend_url = env['JENKINS_UI_URL'] ?: 'http://localhost:8080/'
String admin_email = env['JENKINS_ADMIN']
def master_executors = env['JENKINS_MASTER_EXECUTORS'] ?: 2
def jnlp_slave_port = env['JENKINS_SLAVE_AGENT_PORT'] ?: -1

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
if(j.slaveAgentPort != jnlp_slave_port.toInteger()) {
    if(jnlp_slave_port.toInteger() <= 65535 && jnlp_slave_port.toInteger() >= -1) {
        println "Set JNLP Slave port: ${jnlp_slave_port}"
        j.slaveAgentPort = jnlp_slave_port.toInteger()
        save = true
    }
    else {
        println "WARNING: JNLP port ${jnlp_slave_port} outside of TCP port range.  Must be within -1 <-> 65535.  Nothing changed."
    }
}
//save configuration to disk
if(save) {
    j.save()
    location.save()
}
else {
    println 'Nothing changed.  Jenkins settings already configured.'
}
