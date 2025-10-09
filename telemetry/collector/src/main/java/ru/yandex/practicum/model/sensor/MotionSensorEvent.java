package ru.yandex.practicum.model.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {
	private Integer linkQuality;
	private boolean motion;
	private Integer voltage;

	@Override
	public SensorEventType getType() {
		return SensorEventType.MOTION_SENSOR_EVENT;
	}
}
