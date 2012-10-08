/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;

import jsystem.runner.loader.LoadersManager;

public class MethodElement {
	
	protected Class<?> clazz;
	protected Method method;
	protected String descriptor;
	protected ArrayList<MethodParam> methodParams = new ArrayList<MethodParam>();
	
	public MethodElement(Method method) throws Exception{
		this.method = method;
		this.clazz = method.getDeclaringClass();
		descriptor = AsmUtils.getMethodDescriptor(method);
		initMethodParams();
	}
	public MethodElement(String className, String methodName, String descriptor) throws Exception{
		this.clazz = LoadersManager.getInstance().getLoader().loadClass(className);
		Method[] methods = clazz.getMethods();
		for(Method m: methods){
			if(m.getName().equals(methodName) && descriptor.equals(AsmUtils.getMethodDescriptor(m))){
				method = m;
				break;
			}
		}
		if(method == null){
			throw new Exception("Fail to identify, class: " + className +", method: " + methodName +", descriptor: " + descriptor);
		}
		this.descriptor = descriptor;
		initMethodParams();
	}
	
	
	protected void initMethodParams() throws Exception{
		String[] paramsName = AsmUtils.getParameterNames(method);
		Class<?>[] types = method.getParameterTypes();
		if(types.length != paramsName.length){
			throw new Exception("Wrong parameters names size: " + paramsName.length + " for method " + method.getName());
		}
		for(int i = 0; i < paramsName.length; i++){
			methodParams.add(new MethodParam(types[i], paramsName[i]));
		}
	}
	public String getParametersAsString(){
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < methodParams.size(); i++){
			if(i != 0){
				buf.append(", ");
			}
			buf.append(methodParams.get(i).name);
			buf.append("=${");
			buf.append(methodParams.get(i).name);
			buf.append("}");
		}
		return buf.toString();
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public ArrayList<MethodParam> getMethodParams() {
		return methodParams;
	}

	public void setMethodParams(ArrayList<MethodParam> methodParams) {
		this.methodParams = methodParams;
	}
	public static MethodElement findInObject(Object obj, String methodName, String descriptor) throws Exception{
		Method[] methods = obj.getClass().getMethods();
		for(Method method: methods){
			if(method.getName().equals(methodName)){
				if(AsmUtils.getMethodDescriptor(method).equals(descriptor)){
					return new MethodElement(method);
				}
			}
		}
		throw new Exception("No method " + methodName +" with descriptor " + descriptor + " found");
	}
	public static String getMethodDescriptor(Method method){
		Class<?>[] methodParamTypes = method.getParameterTypes();
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < methodParamTypes.length; i++){
			if(i != 0){
				buf.append(";");
			}
			buf.append(methodParamTypes[i].getName());
		}
		return buf.toString();
	}
	
	public static Method findMethod(Class<?> clazz, String methodName, String descriptor){
		Method[] methods = clazz.getMethods();
		for(int i = 0; i < methods.length; i++){
			if(methods[i].getName().equals(methodName) && getMethodDescriptor(methods[i]).equals(descriptor)){
				return methods[i];
			}
		}
		return null;
	}
	public static Class<?>[] getMethodClassFromDescriptor(String descriptor) throws Exception{
		if(descriptor == null || descriptor.isEmpty()){
			return new Class<?>[0];
		}
		String[] classNames = descriptor.split(";");
		Class<?>[] clazzs = new Class<?>[classNames.length];
		for(int i = 0; i < classNames.length; i++){
			clazzs[i] = BeanUtils.getClassType(classNames[i]);
		}
		return clazzs;
	}
}
