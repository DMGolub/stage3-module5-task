package com.mjc.school.service.validator;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface Validator {

	Set<ConstraintViolation> validate(Object object, Annotation[] parameterAnnotations);
}