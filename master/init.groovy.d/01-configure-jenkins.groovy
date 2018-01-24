import jenkins.model.*

def env = System.getenv()

def config = JenkinsLocationConfiguration.get()

config.setUrl(env.JENKINS_UI_URL)
config.setAdminAddress(env.JENKINS_ADMIN)
config.save()

println("Jenkins URL Set to " + env.JENKINS_UI_URL)
println("Jenkins Admin Address Set to " + env.JENKINS_ADMIN)