package utils

import scala.reflect._

abstract class EnumOf[+Value: ClassTag] extends ObjectFieldsProvider {
  def values = {
    objectObjectFields.getOrElse(instanceObjectFields).collect {
      case v: Value => v
    }
  }

  def valueOfOpt(name: String): Option[Value] = values.find(_.toString == name)

  def valueOf(name: String): Value = valueOfOpt(name).getOrElse {
    throw new IllegalArgumentException(s"No enum value for name $name")
  }
}

trait ObjectFieldsProvider { self =>
  import runtime.universe._

  protected lazy val objectObjectFields: Option[List[Any]] = {
    val mirror = runtimeMirror(self.getClass.getClassLoader)
    val classSymbol = mirror.classSymbol(self.getClass)
    if (classSymbol.isModuleClass) {
      Some(sortedInnerModules(classSymbol).map(m => mirror.reflectModule(m.asModule).instance))
    } else {
      None
    }
  }

  protected def instanceObjectFields: List[Any] = {
    val mirror = runtimeMirror(self.getClass.getClassLoader)
    val classSymbol = mirror.classSymbol(self.getClass)
    sortedInnerModules(classSymbol).map(m => mirror.reflect(self).reflectModule(m.asModule).instance)
  }

  private def sortedInnerModules(classSymbol: ClassSymbol) = {
    classSymbol
      .toType
      .members
      .sorted // Doc: Symbols with the same owner appear in same order of their declarations
      .filter(_.isModule)
  }
}
