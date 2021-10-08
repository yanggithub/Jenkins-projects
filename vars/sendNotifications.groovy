#!/usr/bin/env groovy

/**
 * Send notifications based on build status string
 */
def call(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus = buildStatus ?: 'SUCCESS'

  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details = """<p>${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""

  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESS') {
    color = 'GREEN'
    colorCode = '#00FF00'
  } else {
    color = 'RED'
    colorCode = '#FF0000'
  }

// Send notifications
//   slackSend (color: colorCode, message: summary)
//   hipchatSend (color: color, notify: true, message: summary)
  office365ConnectorSend (
      message: "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (<${env.BUILD_URL}|Open>)", 
      status: buildStatus,
      webhookUrl: 'https://outlook.office.com/webhook/3eafa17a-162c-4802-bfb3-395058a931d1@457d5685-0467-4d05-b23b-8f817adda47c/JenkinsCI/af745ce5f748426389b179b26f54e3db/31402d68-b42a-431a-9a1e-8049c2cf02bf',
      color: colorCode
    )
  emailext (
      subject: subject,
      body: details,
      mimeType: 'text/html',
      replyTo: 'donotreply@birst.com',
      recipientProviders: [culprits(), brokenBuildSuspects(), brokenTestsSuspects(), requestor(), developers()]
    )
}