import jenkins.model.*

def env = System.getenv()

def instance = Jenkins.getInstance()

def anchore = instance.getDescriptorByType(com.anchore.jenkins.plugins.anchore.AnchoreBuilder.DescriptorImpl)

anchore.enabled = true
anchore.engineverify = true
anchore.enginemode = env.ANCHORE_MODE ?: 'anchoreengine'
anchore.engineurl = env.ANCHORE_URL
anchore.engineuser = env.ANCHORE_USER
anchore.enginepass = env.ANCHORE_PASS
anchore.save()

println("Anchore URL Set to " + env.ANCHORE_URL)