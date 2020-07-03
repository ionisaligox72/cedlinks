package com.beust.cedlinks

import com.google.inject.Binder
import com.google.inject.Module
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle

class DropWizardConfig : Configuration() {
    var version: String = "0.1"
}

class CedLinksModule : Module {
    override fun configure(binder: Binder) {
        with(binder) {
            bind(CedLinksService::class.java).toInstance(CedLinksService())
        }
    }
}

class CedLinksApp : Application<DropWizardConfig>() {
    private val module = CedLinksModule()

    override fun initialize(bootstrap: Bootstrap<DropWizardConfig>) {
        bootstrap.addBundle(GuiceBundle.builder()
                .enableAutoConfig(this::class.java.getPackage().getName())
                .build())
    }

    override fun run(configuration: DropWizardConfig, env: Environment) {
//        env.jersey().register(CedLinksService::class.java)
    }

}