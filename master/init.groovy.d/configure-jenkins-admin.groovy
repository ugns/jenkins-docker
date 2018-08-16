import jenkins.model.*
import hudson.security.*

def desiredRealm = System.getenv('JENKINS_REALM') ?: "PRIVATE"
def desiredStrategy = System.getenv('JENKINS_STRATEGY') ?: "PROJECT"

Jenkins j = Jenkins.getInstance()

if (!(j.getSecurityRealm() instanceof HudsonPrivateSecurityRealm) and desiredRealm == "PRIVATE") {
    println
    j.setSecurityRealm(new HudsonPrivateSecurityRealm(false))
}

if (!(j.getAuthorizationStrategy() instanceof ProjectMatrixAuthorizationStrategy) and desiredStrategy == "PROJECT") {
    j.setAuthorizationStrategy(new ProjectMatrixAuthorizationStrategy())
}

def adminUsername = System.getenv('JENKINS_ADMIN_USERNAME') ?: 'admin'
def adminPassword = System.getenv('JENKINS_ADMIN_PASSWORD') ?: 'password'
def currentUsers = j.getSecurityRealm().getAllUsers().collect { it.getId() }
if (!(adminUsername in currentUsers)) {
    def user = j.getSecurityRealm().createAccount(adminUsername, adminPassword)
    user.save
    j.getAuthorizationStrategy().add(Jenkins.ADMINISTER, adminUsername)
}

j.save()
