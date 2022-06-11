# openexchange-giphy-demo-service
Demo service that compares currencies exchange rates on Openexchange and returns GIF image from Gihpy 

Инструкция по использованию:
1) Скачать репозиторий, открыть его в IntelliJ Idea как Gradle проект.
2) Скачать через Gradle необходимые зависимости.
3) Запустить GiphyApplication
4) Открыть в браузере localhost:8080/currency/RUB где вместо RUB может быть код любой другой валюты.

Docker:
1) https://hub.docker.com/layers/gempellm/dockerhub/iamgepush/images/sha256:4bc1467bc49623b3d3201a2ba2b8cf7b2f9b957d737b3a2b778286d617d7e4b6?tab=layers
2) Запустить со стандартными параметрами.
3) Открыть консоль.
4) curl http://localhost:8080/currency/RUB
