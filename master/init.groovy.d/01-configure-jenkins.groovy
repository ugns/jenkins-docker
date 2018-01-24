import jenkins.model.*

def env = System.getenv()

def config = JenkinsLocationConfiguration.get()

config.setUrl(env.JENKINS_UI_URL)
config.setAdminAddress(env.JENKINS_ADMIN)
config.save()

println("Jenkins URL Set to " + env.JENKINS_UI_URL)
println("Jenkins Admin Address Set to " + env.JENKINS_ADMIN)

def instance = Jenkins.getInstance()

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

def rule = Jenkins.instance.getExtensionList(jenkins.security.s2m.MasterKillSwitchConfiguration.class)[0].rule
if(!rule.getMasterKillSwitch()) {
    rule.setMasterKillSwitch(true)
    //dismiss the warning because we don't care (cobertura reporting is broken otherwise)
    Jenkins.instance.getExtensionList(jenkins.security.s2m.MasterKillSwitchWarning.class)[0].disable(true)
    Jenkins.instance.save()
    println 'Disabled agent -> master security for cobertura.'
}
else {
    println 'Nothing changed.  Agent -> master security already disabled.'
}