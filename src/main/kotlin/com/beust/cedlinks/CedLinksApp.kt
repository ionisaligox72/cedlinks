package com.beust.cedlinks

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment

class DropWizardConfig : Configuration() {
    var version: String = "0.1"
}

class CedLinksApp : Application<DropWizardConfig>() {
    override fun run(configuration: DropWizardConfig, env: Environment) {
        listOf(CedLinksService::class.java).forEach {
            env.jersey().register(it)
        }

    }

}