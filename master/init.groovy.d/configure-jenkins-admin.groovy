import jenkins.model.*
import hudson.security.*

Jenkins j = Jenkins.getInstance()

if (j.getSecurityRealm().getClass() == SecurityRealm.None) {
    println("Hudson Private Security Realm enabled")
    j.setSecurityRealm(new HudsonPrivateSecurityRealm(false))
}

if (j.getAuthorizationStrategy().getClass() == AuthorizationStrategy.Unsecured) {
    println("Project Matrix Authorization Strategy enabled")
    j.setAuthorizationStrategy(new ProjectMatrixAuthorizationStrategy())
}

def adminUsername = System.getenv('JENKINS_ADMIN_USERNAME') ?: 'admin'
def adminPassword = System.getenv('JENKINS_ADMIN_PASSWORD') ?: 'password'
def currentUsers = j.getSecurityRealm().getAllUsers().collect { it.getId() }
if (!(adminUsername in currentUsers)) {
    println("Created administrative user ${adminUsername}")
    def user = j.getSecurityRealm().createAccount(adminUsername, adminPassword)
    user.save
    j.getAuthorizationStrategy().add(Jenkins.ADMINISTER, adminUsername)
}

j.save()
