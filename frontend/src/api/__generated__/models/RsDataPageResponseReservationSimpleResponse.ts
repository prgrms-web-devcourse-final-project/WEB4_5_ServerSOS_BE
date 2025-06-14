/* tslint:disable */
/* eslint-disable */
/**
 * PickGO API Document
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { mapValues } from "../runtime"
import type { PageResponseReservationSimpleResponse } from "./PageResponseReservationSimpleResponse"
import {
  PageResponseReservationSimpleResponseFromJSON,
  PageResponseReservationSimpleResponseFromJSONTyped,
  PageResponseReservationSimpleResponseToJSON,
  PageResponseReservationSimpleResponseToJSONTyped,
} from "./PageResponseReservationSimpleResponse"

/**
 *
 * @export
 * @interface RsDataPageResponseReservationSimpleResponse
 */
export interface RsDataPageResponseReservationSimpleResponse {
  /**
   *
   * @type {number}
   * @memberof RsDataPageResponseReservationSimpleResponse
   */
  code?: number
  /**
   *
   * @type {string}
   * @memberof RsDataPageResponseReservationSimpleResponse
   */
  message?: string
  /**
   *
   * @type {PageResponseReservationSimpleResponse}
   * @memberof RsDataPageResponseReservationSimpleResponse
   */
  data?: PageResponseReservationSimpleResponse
}

/**
 * Check if a given object implements the RsDataPageResponseReservationSimpleResponse interface.
 */
export function instanceOfRsDataPageResponseReservationSimpleResponse(
  value: object,
): value is RsDataPageResponseReservationSimpleResponse {
  return true
}

export function RsDataPageResponseReservationSimpleResponseFromJSON(
  json: any,
): RsDataPageResponseReservationSimpleResponse {
  return RsDataPageResponseReservationSimpleResponseFromJSONTyped(json, false)
}

export function RsDataPageResponseReservationSimpleResponseFromJSONTyped(
  json: any,
  ignoreDiscriminator: boolean,
): RsDataPageResponseReservationSimpleResponse {
  if (json == null) {
    return json
  }
  return {
    code: json["code"] == null ? undefined : json["code"],
    message: json["message"] == null ? undefined : json["message"],
    data:
      json["data"] == null
        ? undefined
        : PageResponseReservationSimpleResponseFromJSON(json["data"]),
  }
}

export function RsDataPageResponseReservationSimpleResponseToJSON(
  json: any,
): RsDataPageResponseReservationSimpleResponse {
  return RsDataPageResponseReservationSimpleResponseToJSONTyped(json, false)
}

export function RsDataPageResponseReservationSimpleResponseToJSONTyped(
  value?: RsDataPageResponseReservationSimpleResponse | null,
  ignoreDiscriminator: boolean = false,
): any {
  if (value == null) {
    return value
  }

  return {
    code: value["code"],
    message: value["message"],
    data: PageResponseReservationSimpleResponseToJSON(value["data"]),
  }
}
