
# Fascicolo Sanitario 2.0

# _it-fse-gtw-config_

Il microservizio in oggetto ha la responsabilità di gestire e persistere le configurazioni del Gateway condivise tra tutti i microservizi.

<br/>

## Repos
- [*it-fse-support*](https://github.com/ministero-salute/it-fse-support) - Pagina di supporto FSE 2.0
- [*it-fse-landing*](https://github.com/ministero-salute/it-fse-landing) - Landing page dei repository FSE 2.0

<br/>

## Isolation tests

With Java 21 and Maven, run the opt-in suite without external services:

```sh
mvn -PISOLATION clean test
```

The profile selects JUnit tests tagged `ISOLATION`, which ordinary Maven test
runs exclude. External dependencies are mocked; Spring tests load only the
components under test. JaCoCo reports are generated in `target/site/jacoco/`.

## License

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL_v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
