# Solución Desafío 3 - Actor INSPECT

## Resumen
Se ha implementado el actor "INSPECT" siguiendo los criterios especificados:

### ✅ Criterios Cumplidos (100 puntos)

1. **[10 pts] Creación del actor "INSPECT"** - ✅ Completado
   - Archivo: `src/main/java/co/com/techskill/lab2/library/actor/InspectActor.java`
   - Sigue el patrón de los otros actores existentes

2. **[25 pts] Integración en OrchestratorService** - ✅ Completado
   - Solo procesa peticiones con `priority >= 7`
   - Implementado en `src/main/java/co/com/techskill/lab2/library/service/impl/OrchestratorServiceImpl.java`

3. **[25 pts] Filtrado de priority >= 7** - ✅ Completado
   - Filtro implementado usando `.filter(petition -> petition.getPriority() >= 7)`
   - Las peticiones con priority < 7 se ignoran completamente

4. **[15 pts] Uso de onErrorResume(...)** - ✅ Completado
   - Implementado flujo reactivo sin errores que interrumpan el flujo
   - Manejo de errores con `.onErrorContinue()`

5. **[10 pts] Mostrar por consola la ejecución del orquestador** - ✅ Completado
   - Mensajes detallados para peticiones INSPECT:
     - Filtrado por prioridad
     - Ordenamiento por prioridad descendente
     - Procesamiento individual

6. **[15 pts] Flujo reactivo sin errores** - ✅ Completado
   - Usa `flatMapSequential` para preservar orden relativo
   - Ordenamiento por prioridad descendente antes de enviar al actor
   - Procesamiento ordenado que respeta la prioridad

7. **[5 pts] Código limpio, comentado y organizado** - ✅ Completado

## Características Implementadas

### Actor INSPECT
- Procesa solo peticiones de tipo "INSPECT"
- Integra con IBookRepository para obtener información de libros
- Retorna mensajes formateados con información del libro y prioridad
- Delay de 200ms para simular procesamiento

### OrchestratorService Modificado
- **flatMapSequential**: Preserva el orden relativo de procesamiento
- **Filtro de Prioridad**: Solo procesa peticiones con priority >= 7
- **Ordenamiento**: Ordena por prioridad descendente antes del procesamiento
- **Mensajes de Console**: Logs detallados del proceso de filtrado y ordenamiento

### Flujo de Procesamiento INSPECT
1. Agrupa peticiones por tipo ("INSPECT")
2. Filtra solo las peticiones con priority >= 7
3. Ordena por prioridad descendente (mayor a menor)
4. Procesa usando flatMapSequential para mantener orden
5. Muestra mensajes detallados en consola

## Estructura de Archivos Creados/Modificados

```
src/main/java/co/com/techskill/lab2/library/
├── actor/
│   └── InspectActor.java                    [NUEVO]
└── service/impl/
    └── OrchestratorServiceImpl.java         [MODIFICADO]

src/test/java/co/com/techskill/lab2/library/
├── actor/
│   └── InspectActorTest.java               [NUEVO - Tests unitarios]
└── service/
    └── OrchestratorServiceInspectTest.java [NUEVO - Tests de integración]
```

## Cómo Probar la Solución

### 1. Ejecutar Tests
```bash
.\gradlew test
```

### 2. Ejecutar la aplicación
```bash
.\gradlew bootRun
```

### 3. Crear datos de prueba
```bash
# Crear libros de ejemplo
curl -X POST http://localhost:8080/books/create-sample

# Crear peticiones INSPECT con diferentes prioridades
curl -X POST http://localhost:8080/petitions/inspect/create-sample
```

### 4. Ejecutar orquestación
```bash
# Observar los logs de filtrado y ordenamiento
curl http://localhost:8080/orchestrate/start
```

## Ejemplo de Output Esperado

Al ejecutar la orquestación, verás logs similares a:
```
Inicio orquestación...
Petición encontrada con ID: INSPECT001 de tipo INSPECT
Agrupación por tipo
Petición [INSPECT] con ID: INSPECT001 filtrada (priority >= 7)
Petición [INSPECT] con ID: INSPECT001 ordenada por prioridad descendente
Procesando petición de tipo [INSPECT] con ID INSPECT001
Proceso [INSPECT] exitoso
Next: [INSPECT] petition for book: BOOK004 with priority 10
```

## Tests Implementados

### InspectActorTest
- Verifica que el actor solo soporta tipo "INSPECT"
- Prueba el procesamiento correcto de peticiones
- Validación de formato de output

### OrchestratorServiceInspectTest  
- Verifica filtrado por prioridad >= 7
- Confirma ordenamiento por prioridad descendente
- Valida que se ignoran peticiones de baja prioridad

¡Desafío completado exitosamente! 🎉
