package com.gawel.core.mappers

interface EntityMapper<Entity, DomainModel> {
    fun mapFromEntity(entity: Entity) : DomainModel
    fun mapToEntity(domainModel: DomainModel) : Entity
}