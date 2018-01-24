import jenkins.model.*

def env = System.getenv()

def config = JenkinsLocationConfiguration.get()
def instance = Jenkins.getInstance()

config.setUrl(env.JENKINS_UI_URL)
config.setAdminAddress(env.JENKINS_ADMIN)
config.save()

instance.setNumExecutors(0)
if(!instance.isQuietingDown()) {
    Set<String> agentProtocolsList = ['JNLP4-connect', 'Ping']
    if(!instance.getAgentProtocols().equals(agentProtocolsList)) {
        instance.setAgentProtocols(agentProtocolsList)
        println "Agent Protocols have changed.  Setting: ${agentProtocolsList}"
        instance.save()
    }
    else {
        println "Nothing changed.  Agent Protocols already configured: ${instance.getAgentProtocols()}"
    }
}
else {
    println 'Shutdown mode enabled.  Configure Agent Protocols SKIPPED.'
}

println("Jenkins URL Set to " + env.JENKINS_UI_URL)
println("Jenkins Admin Address Set to " + env.JENKINS_ADMIN)