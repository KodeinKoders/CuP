package net.kodein.cup

@RequiresOptIn("This API is public for CuP Plugins, it should not be used in presentations.")
public annotation class PluginCupAPI()

@RequiresOptIn("This API is internal mto CuP, it may change or disappear without warning.")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS)
public annotation class InternalCupAPI()
