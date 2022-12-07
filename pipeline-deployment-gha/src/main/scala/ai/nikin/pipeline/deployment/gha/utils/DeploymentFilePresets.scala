package ai.nikin.pipeline.deployment.gha.utils

import ai.nikin.pipeline.deployment.gha.models.{DeltaLakeKeeper, SparkApp}

trait DeploymentFilePresets {
  def workflowHeader(workflowName: String, branches: Seq[String]): String =
    s"""
      |name: $workflowName
      |
      |on:
      |  push:
      |    branches: [ ${branches.mkString(",")} ]
      |""".stripMargin

  def runDeltaLakeKeeper(deltaTable: DeltaLakeKeeper): String =
    s"""
      |generate-delta-tables:
      |    runs-on: ubuntu-latest
      |    steps:
      |      - name: Checkout Delta Generation Repository
      |        uses: actions/checkout@v3
      |        with:
      |          repository: ${deltaTable.repository}
      |          token: $${secrets.GH_TOKEN}
      |          path: defaultRepository
      |      - name: Run Delta Lake Tables Generation
      |        working-directory: defaultRepository
      |        run: sbt run
      |""".stripMargin

  def uploadToS3(localPath: String, s3Path: String): String =
    s"""
      |deploy-jar-to-s3:
      |    runs-on: ubuntu-latest
      |    needs: generate-delta-tables
      |    steps:
      |      - uses: actions/checkout@v2
      |      - name: Set up JDK 1.8
      |        uses: actions/setup-java@v1
      |        with:
      |          java-version: 1.8
      |      - name: Build Fat Jar
      |        run: sbt assembly
      |      - name: S3 Upload Jar
      |        uses: tpaschalis/s3-sync-action@master
      |    env:
      |      AWS_S3_BUCKET: $${{ secrets.AWS_S3_BUCKET }}/$s3Path/
      |      AWS_ACCESS_KEY_ID: $${{ secrets.AWS_ACCESS_KEY_ID }}
      |      AWS_SECRET_ACCESS_KEY: $${{ secrets.AWS_SECRET_ACCESS_KEY }}
      |      AWS_REGION: 'eu-west-1'
      |      FILE: '$localPath'
      |""".stripMargin

  def runSparkApp(clusterID: String, sparkApp: SparkApp): String =
    s"""
      |run-job:
      |    runs-on: ubuntu-latest
      |    needs: deploy-jar-to-s3
      |    steps:
      |      - name: Configure AWS CLI
      |        run: |
      |          sudo apt-get -y install awscli
      |          aws configure set aws_access_key_id $${{ secrets.AWS_ACCESS_KEY_ID }}
      |          aws configure set aws_secret_access_key $${{ secrets.AWS_SECRET_ACCESS_KEY }}
      |          aws configure set default.region eu-west-1
      |      - name: Run Spark Job
      |        run: |
      |        aws emr add-steps --cluster-id "$clusterID" --steps Type=Spark,Name="${sparkApp
        .name}",ActionOnFailure=CONTINUE,Args=[--class,${sparkApp
        .mainClass},s3://nikinconf/jar/spark-aggregator.jar, --inputPath, ${sparkApp
        .inputPath}, --outputPath, ${sparkApp.outputPath}]
      |""".stripMargin
}
