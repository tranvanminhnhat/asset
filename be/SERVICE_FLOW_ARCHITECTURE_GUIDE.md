# Service Flow Architecture Guide

## Standard Entry Flow

```java
@Transactional
public Response execute(Request request) {
    validateRequest(request);
    Context ctx = loadData(request);
    processBusiness(ctx);
    return createResponse(ctx);
}
```

## Standard Business Flow

```java
private void processBusiness(Context ctx) {
    validateBusiness(ctx);
    prepare(ctx);
    execute(ctx);
    persist(ctx);
    afterProcess(ctx);
}
```

## Method Responsibilities

### `validateRequest(request)`
- Basic request format validation only
- null/empty/format checks
- no business logic
- no database query

### `loadData(request)`
- Load data upfront
- DB query / external API / config
- avoid duplicate queries
- return `Context`

### `validateBusiness(ctx)`
- Business rule validation only
- status/permission/domain checks
- no request format validation

### `prepare(ctx)`
- Mapping/calculation/normalization
- build temporary values

### `execute(ctx)`
- Main business logic only

### `persist(ctx)`
- save/update/delete to DB
- group persistence logic in one place

### `afterProcess(ctx)`
- Side effects only
- logging, notification, cache, messaging

### `createResponse(ctx)`
- map `Context`/entity to response
- no business logic

## Constraints
- Keep existing business behavior unchanged
- Do not modify transaction behavior
- Do not change DB queries
- Do not remove validations
- Keep methods focused and readable
- Keep code in the same service class

## Service and Controller Granularity
- Each service class must expose exactly one public method (use-case style).
- Name public method consistently as `execute(...)` where possible.
- If a feature has multiple APIs, split into multiple service classes (one use-case per service).
- Split controllers accordingly so each controller handles a focused API set.
- Keep all services/controllers inside the same feature folder cluster.
- Do not move feature code into unrelated packages.

## Data Layer Placement
- All JPA entities must be placed under `src/main/java/com/minhnhat/demo/db/entity`.
- All repositories must be placed under `src/main/java/com/minhnhat/demo/db/repository`.
- Feature services/controllers import from centralized `db` packages instead of feature-local entity/repository packages.
- If multiple features need methods for the same entity, keep one repository for that entity and merge methods into it.

## Context Pattern
- Use private static inner `Context` class when needed
- Include only fields used by the flow

