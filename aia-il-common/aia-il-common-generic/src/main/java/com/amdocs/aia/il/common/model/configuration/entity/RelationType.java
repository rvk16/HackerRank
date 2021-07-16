package com.amdocs.aia.il.common.model.configuration.entity;

public enum RelationType {
	ROOT,
	ONE_TO_ONE,
	ONE_TO_MANY,
	MANY_TO_ONE,
	MANY_TO_MANY,
	ONE_TO_MANY_SCD, // time based lookup (results in creation on new virtual entities expressing the relation)
					 // we're talking about one to many from the lookup perspective
}
