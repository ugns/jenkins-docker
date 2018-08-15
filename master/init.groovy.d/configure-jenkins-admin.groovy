import jenkins.model.*
import hudson.security.*


Jenkins j = Jenkins.getInstance()

if (!(j.getSecurityRealm())) {
    j.setSecurityRealm(new HudsonPrivateSecurityRealm(false))
}

if (!(j.getAuthorizationStrategy())) {
    j.setAuthorizationStrategy(new ProjectMatrixAuthorizationStrategy())
}

def adminUsername = System.getenv('JENKINS_ADMIN_USERNAME') ?: 'admin'
def adminPassword = System.getenv('JENKINS_ADMIN_PASSWORD') ?: 'password'
def currentUsers = jenkins.getSecurityRealm().getAllUsers().collect { it.getId() }
if (!(adminUsername in currentUsers)) {
    def user = j.getSecurityRealm().createAccount(adminUsername, adminPassword)
    user.save
    j.getAuthorizationStrategy().add(Jenkins.ADMINISTER, adminUsername)
}

j.save()
