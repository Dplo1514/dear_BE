package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.model.ResponseTag;
import com.sparta.hh99_actualproject.repository.ResponseTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.sparta.hh99_actualproject.dto.MemberResponseDto.ResTagResponseDto;

@Service
@RequiredArgsConstructor
public class ResponseTagService {
    private final ResponseTagRepository responseTagRepository;

    public ResTagResponseDto findMemberMostResTag(String memberId) {
        //resTag 추출 로직
        //멤버가 획득한 response태그들을 찾아온다.

        ResponseTag responseTag = responseTagRepository.findByMemberId(memberId);

        if (responseTag != null){
        //return값을 담을 Dto
        ResTagResponseDto resTagResponseDto = new ResTagResponseDto();

        //TagNumber별 리턴해야할 태그 값을 set해줄 Map
        ConcurrentHashMap<Integer, String> resTagMapContent = new ConcurrentHashMap<>();
        resTagMapContent.put(1, "공감을 잘해줬어요");
        resTagMapContent.put(2, "대화가 즐거웠어요");
        resTagMapContent.put(3, "감수성이 풍부했어요");
        resTagMapContent.put(4, "시원하게 팩트폭격을 해줘요");
        resTagMapContent.put(5, "명쾌한 해결책을 알려줘요");

        //ResTag별로 인덱스를 지정하는 방법
        //1. 맵에 Res태그별 키값을 지정해준다.
        //2. value인 Res태그중 가장 큰 값을 두개 찾는다.
        //3. 가장 큰 값의 key 두개로 String맵의 key를 인덱스한다.
        ConcurrentHashMap<Integer, Integer> resTagIdx = new ConcurrentHashMap<>();

        resTagIdx.put(1, responseTag.getResTag1Num());
        resTagIdx.put(2, responseTag.getResTag2Num());
        resTagIdx.put(3, responseTag.getResTag3Num());
        resTagIdx.put(4, responseTag.getResTag4Num());
        resTagIdx.put(5, responseTag.getResTag5Num());


        //value를 기준으로 오름차순이 가능하게하는 comparingByValue함수를 사용하기위해
        //List에 Map.Entry로 resTagIdx를 할당해준다.
        //Map.Entry : Map을 For 문에서 돌려줄 경우 , Map에서 strem , 정렬 등을 필요할 때 사용하는 인터페이스
        //리스트의 Iterator와 비슷한 개념이라 생각하면 좋을 것 같다.
        //1. Map.Entry를 제네릭스로 받는 리스트 객체를 생성
        //2. resTagIdx.map.entrySet() : 맵의 K , V 전체를 가져와서 리스트에 할당한다..
        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(resTagIdx.entrySet());

        //Map.Entry.comparingByValue() : 해당 map의 value값을 기준으로 정렬한다.
        entryList.sort(Map.Entry.comparingByValue());


        //value값으로 정렬된 entryList의 3 , 4번째는 resTagIdx의 key 중 valye값이 가장 큰 키 2개를 의미
        //이는 resTag의 갯수가 가장 많은 것의 key를 의미한다.
        //해당 키로 resTag별로 미리 리턴 값(value)을 지정해준 map의 idx함으로써 가장 큰 값 두개의 String을 인덱스할 수 있다.
        resTagResponseDto.setResTag1(resTagMapContent.get(entryList.get(3).getKey()));
        resTagResponseDto.setResTag2(resTagMapContent.get(entryList.get(4).getKey()));
            return resTagResponseDto;
        }else {
            return null;
        }
    }
}
