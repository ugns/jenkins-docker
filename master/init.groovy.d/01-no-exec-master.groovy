import jenkins.model.*

def instance = Jenkins.get()

instance.setNumExecutors(0)