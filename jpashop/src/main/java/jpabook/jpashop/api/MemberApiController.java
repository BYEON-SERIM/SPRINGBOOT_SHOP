package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;


    /**
     *
     * 회원 목록 API
     */
    @GetMapping("/api/v1/members")
    public Result memberV1(){
        List<Member> findmembers = memberService.findMembers();
        List<MemberDto> collect = findmembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);

    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }



    /**
     *
     * 회원 등록 API
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid CreateMemberResquest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

        @Data
        @AllArgsConstructor
        static class CreateMemberResponse{
            private  Long id;               //alt+insert  : 생성자 만들기

        }

        @Data
        static class CreateMemberResquest{
            @NotEmpty
            private String name;
        }



    /**
     *
     * 회원 수정 API
     */
    @PutMapping("/api/v1/members/{id}")
    public UpadateMemberResponse updateMemberV1(@PathVariable("id") Long id,
                                                @RequestBody @Valid UpadateMemberRequest request){

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return new UpadateMemberResponse(findMember.getId(), findMember.getName());

    }


    @Data
    @AllArgsConstructor
    static class UpadateMemberResponse{
        private  Long id;
        private String name;
    }

    @Data
    static class UpadateMemberRequest{
        @NotEmpty
        private String name;
    }






}
