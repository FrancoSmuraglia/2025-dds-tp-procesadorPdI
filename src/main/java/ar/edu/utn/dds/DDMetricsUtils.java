package ar.edu.utn.dds;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.datadog.DatadogConfig;
import io.micrometer.datadog.DatadogMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class DDMetricsUtils {
	private static final Logger log = LoggerFactory.getLogger(DDMetricsUtils.class);

	private final StepMeterRegistry registry;

	public DDMetricsUtils(String appTag) {
		log.info("Initializing Datadog metrics for app: {}", appTag);

		var config = new DatadogConfig() {
			@Override
			public Duration step() {
				return Duration.ofSeconds(10);
			}

			@Override
			public String apiKey() {
				String apiKey = System.getenv("DDAPI");
				if (apiKey == null || apiKey.trim().isEmpty()) {
					log.warn("⚠️  DDAPI environment variable not set! Datadog metrics will not work.");
					return "dummy-key";
				}
				log.info("✅ Datadog API key configured correctly");
				return apiKey;
			}

			@Override
			public String uri() {
				return "https://api.us5.datadoghq.com";
			}

			@Override
			public String get(String k) {
				return null;
			}
		};

		registry = new DatadogMeterRegistry(config, Clock.SYSTEM);
		registry.config().commonTags("app", appTag, "environment", "development", "service", "pdi");

		log.info("Datadog registry created with tags: app={}, environment=development, service=pdi", appTag);

		initInfraMonitoring();
	}

	public StepMeterRegistry getRegistry() {
		return registry;
	}

	private void initInfraMonitoring() {
		log.info("Initializing infrastructure monitoring metrics...");

		try (var jvmGcMetrics = new JvmGcMetrics();
			 var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()) {
			jvmGcMetrics.bindTo(registry);
			jvmHeapPressureMetrics.bindTo(registry);
		}
		new JvmMemoryMetrics().bindTo(registry);
		new ProcessorMetrics().bindTo(registry);
		new FileDescriptorMetrics().bindTo(registry);

		log.info("✅ Infrastructure monitoring metrics initialized");
	}
}