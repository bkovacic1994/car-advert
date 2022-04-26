package controllers

import models.CarItem
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import scala.collection.mutable


@Singleton
class CarController @Inject()(val controllerComponents: ControllerComponents)
extends BaseController {
    
    val carList = new mutable.ListBuffer[CarItem]()
    carList += CarItem(1, "test", true)
    carList += CarItem(2, "drugi test", false)
    carList += CarItem(3, "treci test", false)
    carList += CarItem(4, "cetvrti test", false)

    implicit val carListJson = Json.format[CarItem]
     

    def getAllCars(): Action[AnyContent] = Action {
        if (carList.isEmpty) {
            NoContent
        } else {
            Ok(Json.toJson(carList))
        }
    }

    def getOneCar(itemId: Long)= Action {
        val foundItem = carList.find(_.id == itemId)
        foundItem match {
            case Some(item) => Ok(Json.toJson(item))
            case None => NotFound
        }
    }

    def deleteOneCar(itemId: Long)= Action{
        val foundItem = carList.find(_.id == itemId)
        foundItem match {
            case Some(item) => carList-=item     
            case None => NotFound
        }
    }
    
    def deleteAllCars()= Action{
        if (carList.isEmpty) {
            NoContent
        } else {
            carList.clear()
        }
    }
    
    def addCar() = Action { implicit request =>
        val content = request.body
        val jsonObject = content.asJson

        val carListItem: Option[NewCarListItem] = jsonObject.flatMap(Json.fromJson[NewCarListItem](_).asOpt)

        carListItem match {
            case Some(newItem) =>
                val nextId = carList.map(_.id).max + 1
                val toBeAdded = carListItem(nextId, newItem.description, false)
                todoList += toBeAdded
                Created(Json.toJson(toBeAdded))
            case None =>
                BadRequest
        }
    }

}