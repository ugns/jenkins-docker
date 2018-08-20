// https://mvnrepository.com/artifact/org.yaml/snakeyaml
@Grapes(
    @Grab(group='org.yaml', module='snakeyaml', version='1.21')
)

import org.yaml.snakeyaml.Yaml
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult
import com.amazonaws.services.ecs.AmazonECSClient
import com.amazonaws.services.ecs.AmazonECSClientBuilder
import com.amazonaws.services.ecs.model.DescribeClustersRequest
import com.amazonaws.services.ecs.model.DescribeClustersResult
import com.amazonaws.services.ecs.model.LaunchType
import com.amazonaws.util.EC2MetadataUtils
import com.cloudbees.jenkins.plugins.amazonecs.ECSCloud
import com.cloudbees.jenkins.plugins.amazonecs.ECSTaskTemplate
import com.cloudbees.jenkins.plugins.amazonecs.ECSTaskTemplate.MountPointEntry
import com.cloudbees.jenkins.plugins.amazonecs.ECSTaskTemplate.LogDriverOption
import hudson.model.*
import hudson.tools.*
import jenkins.model.*

import java.util.logging.Logger

private String getRegion() {
  return EC2MetadataUtils.instanceInfo.region
}

private String queryClusterArn(String clusterName) {
  AmazonECSClient client = AmazonECSClientBuilder.standard().build()
  DescribeClustersRequest request = new DescribeClustersRequest().withClusters(clusterName)
  DescribeClustersResult result = client.describeClusters(request)
  if ((cluster = result.getClusters().first())) {
    return cluster.getClusterArn()
  }
  else {
    Logger.global.info("Unable to locate ECS Cluster ${clusterName}")
  }
}

private String queryTunnel(String elbName) {
	AmazonElasticLoadBalancingClient client = new AmazonElasticLoadBalancingClientBuilder().standard().build()
  DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest().withLoadBalancerNames(elbName);
  DescribeLoadBalancersResult result = client.describeLoadBalancers(request);
  if ((elb = result.getLoadBalancerDescriptions().first())) {
    return "${elb.DNSName}:50000"
  }
	else {
  	Logger.global.info("Unable to locate ELB ${elbName}")
	}
}

private MountPointEntry createMountPoint(String name, String sourcePath, String containerPath, Boolean readOnly = false) {
  new MountPointEntry(name=name, sourcePath=sourcePath, containerPath=containerPath, readOnly=readOnly)
}

private LogDriverOption createLogDriverOption(String name, String value) {
  new LogDriverOption(name=name, value=value)
}

private void configureCloud() {
  try {
    env = System.getenv()
    ECS_CLOUD_YAML = env['ECS_CLOUD_YAML'] ?: "${env['JENKINS_HOME']}/init.groovy.d/amazon-ecs.yaml"
    config = new Yaml().load(new File(ECS_CLOUD_YAML).text)
    clouds = config.clouds

    Jenkins.instance.clouds.clear()
    clouds.each{cloud ->
      def templates = cloud.templates.collect{
        def mounts = it.mountPoints.collect{createMountPoint(it.name, it.sourcePath, it.containerPath, it.readOnly)}
        def logOptions = it.logDriverOptions.collect{createLogDriverOption(it.name, it.value)}
        def ecsTemplate = new ECSTaskTemplate(
          it.name,          // templateName
          it.label,         // label
          null,             // taskDefinitionOverride
          it.image,         // image
          LaunchType.EC2.toString(),  // launchType
          "/home/jenkins",  // remoteFSRoot
          it.memory,        // memory
          it.memoryReservation, // memoryReservation
          it.cpu,           // cpu
          null,             // subnets
          null,             // securityGroups
          false,            // assignPublicIp
          false,            // privileged
          null,             // containerUser
          logOptions,       // logDriverOptions
          null,             // environments
          null,             // extraHosts
          mounts,           // mountPoints
          null              // portMappings
        )
        ecsTemplate.setLogDriver(it.logDriver)
        ecsTemplate
      }

      def ecsCloud = new ECSCloud(
              name = cloud.name,
              templates = templates,
              credentialsId = '',
              cluster = queryClusterArn(cloud.cluster),
              regionName = region,
              jenkinsUrl = '',
              slaveTimoutInSeconds = -1
      )
      if (cloud.tunnel) {
        ecsCloud.tunnel = queryTunnel(cloud.tunnel)
      }
      Jenkins.instance.clouds.add(ecsCloud)
    }
  } catch (com.amazonaws.SdkClientException e) {
    Logger.global.severe({ e.message })
  } catch (java.io.FileNotFoundException e) {
    Logger.global.severe({ e.message })
  }
}

configureCloud()

Jenkins.instance.save()
