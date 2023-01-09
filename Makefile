deploy:
	./gradlew jsBrowserDistribution
	rm -rf breakout-server/src/main/resources/static/www
	mv build/distributions breakout-server/src/main/resources/static/www
