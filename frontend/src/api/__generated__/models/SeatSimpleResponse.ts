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
/**
 *
 * @export
 * @interface SeatSimpleResponse
 */
export interface SeatSimpleResponse {
  /**
   *
   * @type {number}
   * @memberof SeatSimpleResponse
   */
  id?: number
  /**
   *
   * @type {string}
   * @memberof SeatSimpleResponse
   */
  areaName?: string
  /**
   *
   * @type {string}
   * @memberof SeatSimpleResponse
   */
  row?: string
  /**
   *
   * @type {number}
   * @memberof SeatSimpleResponse
   */
  number?: number
  /**
   *
   * @type {string}
   * @memberof SeatSimpleResponse
   */
  status?: SeatSimpleResponseStatusEnum
  /**
   *
   * @type {Date}
   * @memberof SeatSimpleResponse
   */
  createdAt?: Date
}

/**
 * @export
 */
export const SeatSimpleResponseStatusEnum = {
  Pending: "PENDING",
  Reserved: "RESERVED",
  Released: "RELEASED",
} as const
export type SeatSimpleResponseStatusEnum =
  (typeof SeatSimpleResponseStatusEnum)[keyof typeof SeatSimpleResponseStatusEnum]

/**
 * Check if a given object implements the SeatSimpleResponse interface.
 */
export function instanceOfSeatSimpleResponse(
  value: object,
): value is SeatSimpleResponse {
  return true
}

export function SeatSimpleResponseFromJSON(json: any): SeatSimpleResponse {
  return SeatSimpleResponseFromJSONTyped(json, false)
}

export function SeatSimpleResponseFromJSONTyped(
  json: any,
  ignoreDiscriminator: boolean,
): SeatSimpleResponse {
  if (json == null) {
    return json
  }
  return {
    id: json["id"] == null ? undefined : json["id"],
    areaName: json["AreaName"] == null ? undefined : json["AreaName"],
    row: json["row"] == null ? undefined : json["row"],
    number: json["number"] == null ? undefined : json["number"],
    status: json["status"] == null ? undefined : json["status"],
    createdAt:
      json["createdAt"] == null ? undefined : new Date(json["createdAt"]),
  }
}

export function SeatSimpleResponseToJSON(json: any): SeatSimpleResponse {
  return SeatSimpleResponseToJSONTyped(json, false)
}

export function SeatSimpleResponseToJSONTyped(
  value?: SeatSimpleResponse | null,
  ignoreDiscriminator: boolean = false,
): any {
  if (value == null) {
    return value
  }

  return {
    id: value["id"],
    AreaName: value["areaName"],
    row: value["row"],
    number: value["number"],
    status: value["status"],
    createdAt:
      value["createdAt"] == null ? undefined : value["createdAt"].toISOString(),
  }
}
