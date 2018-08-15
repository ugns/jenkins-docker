import jenkins.*
import hudson.*
import hudson.model.*
import jenkins.model.*
import hudson.security.*

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
def adminUsername = System.getenv('JENKINS_ADMIN_USERNAME') ?: 'admin'
def adminPassword = System.getenv('JENKINS_ADMIN_PASSWORD') ?: 'password'
hudsonRealm.createAccount(adminUsername, adminPassword)

Jenkins j = Jenkins.getInstance()
j.setSecurityRealm(hudsonRealm)
j.save()

def strategy = new ProjectMatrixAuthorizationStrategy()

// Setting Admin Permissions
strategy.add(Jenkins.ADMINISTER, adminUsername)
j.setAuthorizationStrategy(strategy)
j.save()
