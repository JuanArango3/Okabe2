# Okabe

Este proyecto es un bot de música para Discord desarrollado con Spring Boot, [JDA](https://github.com/discord-jda/JDA)
y [lavaplayer](https://github.com/lavalink-devs/lavaplayer). 
## Características

- Reproducción de música desde youtube
- Soporte a links directos, búsqueda y playlist
- Integrado con los comandos de discord

## Configuración

El bot puede ejecutarse de dos maneras:

### 1. Usando Docker

1. Construir la imagen de Docker:
    ```sh
    docker build -t okabe .
    ```

2. Ejecutar el contenedor de Docker especificando el token del bot de Discord:
    ```sh
    docker run -e BOT_TOKEN=your_discord_bot_token okabe
    ```

### 2. Desde tu máquina (sin Docker)
Modificando el archivo `application.properties`

1. Editar el archivo `src/main/resources/application.properties` y agregar el token del bot de Discord generado
desde el [Developer portal](https://discord.com/developers/applications):
    ```properties
    app.token=your_discord_bot_token
    ```

2. Ejecutar la aplicación Spring Boot:
    ```sh
    mvn spring-boot:run
    ```

## Uso

Una vez que el bot esté en funcionamiento, puede invitarlo a su servidor de Discord y utilizar los comandos disponibles para controlar la reproducción de música.

## Contribuciones

Las contribuciones son bienvenidas. Por favor, abra un issue o un pull request para discutir cualquier cambio.

## Licencia

Este proyecto está bajo la licencia MIT. Consulte el archivo [LICENSE](./LICENSE) para obtener más detalles.
